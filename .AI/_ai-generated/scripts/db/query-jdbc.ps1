param(
    [Parameter(Position = 0)]
    [string] $Sql,

    [string] $FilePath,

    [ValidateSet('h2', 'mysql', 'mariadb', 'jdbc')]
    [string] $DbType = 'jdbc',

    [string] $JdbcUrl,

    [string] $User = '',

    [string] $Password = '',

    [string] $DriverJar,

    [int] $MaxRows = 200,

    [ValidateRange(1, 100)]
    [int] $RetryCount = 8,

    [ValidateRange(0, 60000)]
    [int] $RetryDelayMillis = 500,

    [switch] $RefreshSchemaCache
)

$ErrorActionPreference = 'Stop'

function Resolve-RepoRoot {
    $current = (Get-Location).Path
    while ($current) {
        if (Test-Path -LiteralPath (Join-Path $current 'AI.md')) {
            return $current
        }

        $parent = Split-Path -Parent $current
        if ($parent -eq $current) {
            break
        }
        $current = $parent
    }

    throw 'Cannot find repository root with AI.md.'
}

function Find-LatestJar {
    param(
        [string[]] $RelativeRoots,
        [string] $Pattern
    )

    $gradleCache = Join-Path $env:USERPROFILE '.gradle\caches\modules-2\files-2.1'
    foreach ($relativeRoot in $RelativeRoots) {
        $root = Join-Path $gradleCache $relativeRoot
        if (-not (Test-Path -LiteralPath $root)) {
            continue
        }

        $jar = Get-ChildItem -Path $root -Recurse -Filter $Pattern |
            Where-Object { $_.Name -notlike '*sources*' -and $_.Name -notlike '*javadoc*' } |
            Sort-Object LastWriteTime -Descending |
            Select-Object -First 1

        if ($jar) {
            return $jar.FullName
        }
    }

    return $null
}

function Read-JsonFile {
    param([string] $Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    try {
        return Get-Content -LiteralPath $Path -Raw -Encoding UTF8 | ConvertFrom-Json
    }
    catch {
        return $null
    }
}

function Write-JsonFile {
    param(
        [string] $Path,
        [object] $Value
    )

    $parent = Split-Path -Parent $Path
    if (-not (Test-Path -LiteralPath $parent)) {
        New-Item -ItemType Directory -Path $parent | Out-Null
    }

    $Value | ConvertTo-Json -Depth 5 | Set-Content -LiteralPath $Path -Encoding UTF8
}

function Resolve-DriverJar {
    param(
        [string] $Type,
        [string] $CachePath
    )

    if ($DriverJar) {
        $resolved = Resolve-Path -LiteralPath $DriverJar
        return $resolved.Path
    }

    $cache = Read-JsonFile -Path $CachePath
    if ($cache -and $cache.$Type -and (Test-Path -LiteralPath $cache.$Type)) {
        return $cache.$Type
    }

    $resolvedJar = $null
    if ($Type -eq 'h2') {
        $resolvedJar = Find-LatestJar -RelativeRoots @('com.h2database\h2') -Pattern 'h2-*.jar'
    }
    elseif ($Type -eq 'mysql') {
        $resolvedJar = Find-LatestJar -RelativeRoots @('com.mysql\mysql-connector-j', 'mysql\mysql-connector-java') -Pattern 'mysql-connector-*.jar'
    }
    elseif ($Type -eq 'mariadb') {
        $resolvedJar = Find-LatestJar -RelativeRoots @('org.mariadb.jdbc\mariadb-java-client') -Pattern 'mariadb-java-client-*.jar'
    }
    else {
        throw 'DbType jdbc requires -DriverJar.'
    }

    if ($resolvedJar) {
        if (-not $cache) {
            $cache = [pscustomobject]@{}
        }
        $cache | Add-Member -NotePropertyName $Type -NotePropertyValue $resolvedJar -Force
        Write-JsonFile -Path $CachePath -Value $cache
    }

    return $resolvedJar
}

function Assert-ReadOnlySql {
    param([string] $Statement)

    $trimmed = $Statement.TrimStart()
    if ($trimmed -notmatch '^(?is)(select|show|with|explain|describe|desc)\b') {
        throw 'Only read-only SQL is allowed by this support script.'
    }

    if ($Statement -match '(?is)\b(insert|update|delete|merge|create|alter|drop|truncate|grant|revoke|runscript|script|backup|shutdown)\b') {
        throw 'Potential write, DDL, or database control SQL was detected. This support script does not execute it.'
    }
}

function Assert-H2FileExists {
    param([string] $Url)

    if ($Url -notmatch '^jdbc:h2:file:(?<path>[^;]+)') {
        return
    }

    $dbPath = $Matches['path']
    if ($dbPath.StartsWith('./') -or $dbPath.StartsWith('.\')) {
        $dbPath = Join-Path (Get-Location).Path $dbPath.Substring(2)
    }

    if (-not [System.IO.Path]::IsPathRooted($dbPath)) {
        $dbPath = Join-Path (Get-Location).Path $dbPath
    }

    $dbFile = "$dbPath.mv.db"
    if (-not (Test-Path -LiteralPath $dbFile)) {
        throw "H2 DB file does not exist. Refusing to create an empty DB: $dbFile"
    }
}

function Test-LastSuccessMatches {
    param(
        [object] $LastSuccess,
        [string] $Type,
        [string] $Url,
        [string] $LoginUser
    )

    return $LastSuccess `
        -and $LastSuccess.dbType -eq $Type `
        -and $LastSuccess.jdbcUrl -eq $Url `
        -and $LastSuccess.user -eq $LoginUser `
        -and $LastSuccess.driverJar `
        -and (Test-Path -LiteralPath $LastSuccess.driverJar)
}

function Write-LastSuccess {
    param(
        [string] $Path,
        [string] $Type,
        [string] $Url,
        [string] $LoginUser,
        [string] $ResolvedDriverJar
    )

    Write-JsonFile -Path $Path -Value ([pscustomobject]@{
        dbType = $Type
        jdbcUrl = $Url
        user = $LoginUser
        driverJar = $ResolvedDriverJar
        verifiedAt = (Get-Date).ToString('o')
    })
}

function Get-SchemaSql {
    param([string] $Type)

    if ($Type -eq 'h2') {
        return @'
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'PUBLIC'
ORDER BY TABLE_NAME, ORDINAL_POSITION
'@
    }

    if ($Type -eq 'mysql' -or $Type -eq 'mariadb') {
        return @'
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME, ORDINAL_POSITION
'@
    }

    throw "Schema cache is not supported for DbType '$Type'."
}

function Write-SchemaCache {
    param(
        [string] $Path,
        [string] $Type,
        [string] $Url,
        [string] $LoginUser,
        [string[]] $Lines
    )

    $normalizedLines = New-Object System.Collections.Generic.List[string]
    foreach ($line in @($Lines)) {
        if ($null -ne $line) {
            foreach ($part in ([string]$line -split "\r?\n")) {
                $normalizedLines.Add($part)
            }
        }
    }

    $tables = [ordered]@{}
    foreach ($line in $normalizedLines) {
        if (-not $line -or $line -match '^(TABLE_NAME\t|ROWS\t|\(\d+ rows?,|\s*$)') {
            continue
        }

        $values = $line.Split([char]9)
        if ($values.Count -lt 4) {
            continue
        }

        $tableName = [string]$values[0]
        if (-not $tables.Contains($tableName)) {
            $tables[$tableName] = New-Object System.Collections.Generic.List[object]
        }

        $tables[$tableName].Add([pscustomobject]@{
            name = $values[1]
            type = $values[2]
            nullable = $values[3]
        })
    }

    Write-JsonFile -Path $Path -Value ([pscustomobject]@{
        dbType = $Type
        jdbcUrl = $Url
        user = $LoginUser
        refreshedAt = (Get-Date).ToString('o')
        tableCount = $tables.Count
        tables = $tables
    })
}

function Ensure-RunnerCompiled {
    param(
        [string] $RunnerDir,
        [string] $RunnerSource
    )

    if (-not (Test-Path -LiteralPath $RunnerDir)) {
        New-Item -ItemType Directory -Path $RunnerDir | Out-Null
    }

    $runnerFile = Join-Path $RunnerDir 'QueryJdbcRunner.java'
    $classFile = Join-Path $RunnerDir 'QueryJdbcRunner.class'
    $sourceHashFile = Join-Path $RunnerDir 'QueryJdbcRunner.sha256'
    $sourceHash = [Convert]::ToHexString([System.Security.Cryptography.SHA256]::HashData([System.Text.Encoding]::UTF8.GetBytes($RunnerSource)))
    $cachedHash = if (Test-Path -LiteralPath $sourceHashFile) { Get-Content -LiteralPath $sourceHashFile -Raw -Encoding UTF8 } else { '' }

    if ((-not (Test-Path -LiteralPath $classFile)) -or $cachedHash.Trim() -ne $sourceHash) {
        Set-Content -LiteralPath $runnerFile -Value $RunnerSource -Encoding UTF8
        javac -encoding UTF-8 -d $RunnerDir $runnerFile
        if ($LASTEXITCODE -ne 0) {
            exit $LASTEXITCODE
        }
        Set-Content -LiteralPath $sourceHashFile -Value $sourceHash -Encoding ASCII
    }

    return $RunnerDir
}

if (-not $Sql -and -not $FilePath -and -not $RefreshSchemaCache) {
    throw 'Provide either SQL text or -FilePath.'
}

if ($Sql -and $FilePath) {
    throw 'Provide only one of SQL text or -FilePath.'
}

$repoRoot = Resolve-RepoRoot
Set-Location -LiteralPath $repoRoot

$cacheDir = Join-Path $repoRoot '.AI\_ai-generated\.cache\query-jdbc'
$driverCachePath = Join-Path $cacheDir 'driver-cache.json'
$lastSuccessPath = Join-Path $cacheDir 'last-success.json'
$schemaCachePath = Join-Path $cacheDir 'schema-cache.json'
$lastSuccess = Read-JsonFile -Path $lastSuccessPath

if (-not $JdbcUrl) {
    if ($lastSuccess -and $lastSuccess.jdbcUrl) {
        $DbType = $lastSuccess.dbType
        $JdbcUrl = $lastSuccess.jdbcUrl
        if (-not $User) {
            $User = $lastSuccess.user
        }
    }
    else {
        throw 'Provide -JdbcUrl, or run a successful query once so last-success cache can be used.'
    }
}

$sqlToRun = if ($RefreshSchemaCache) { Get-SchemaSql -Type $DbType } else { $Sql }
if ($FilePath) {
    $resolvedFile = Resolve-Path -LiteralPath $FilePath
    $sqlToRun = Get-Content -LiteralPath $resolvedFile -Raw -Encoding UTF8
}

Assert-ReadOnlySql -Statement $sqlToRun

if ($DbType -eq 'h2') {
    Assert-H2FileExists -Url $JdbcUrl
}

$resolvedDriverJar = if (Test-LastSuccessMatches -LastSuccess $lastSuccess -Type $DbType -Url $JdbcUrl -LoginUser $User) {
    $lastSuccess.driverJar
}
else {
    Resolve-DriverJar -Type $DbType -CachePath $driverCachePath
}
if (-not $resolvedDriverJar) {
    throw "Cannot find JDBC driver jar for DbType '$DbType'. Run Gradle dependency resolution first or pass -DriverJar."
}

$runnerSource = @'
import java.sql.*;

public class QueryJdbcRunner {
    public static void main(String[] args) throws Exception {
        String url = args[0];
        String user = args[1];
        String password = args[2];
        String sql = args[3];
        int maxRows = Integer.parseInt(args[4]);
        int retryCount = Integer.parseInt(args[5]);
        int retryDelayMillis = Integer.parseInt(args[6]);

        try (Connection connection = connectWithRetry(url, user, password, retryCount, retryDelayMillis);
             Statement statement = connection.createStatement()) {
            statement.setMaxRows(maxRows);
            boolean hasResultSet = statement.execute(sql);
            if (!hasResultSet) {
                System.out.println("UPDATE_COUNT\t" + statement.getUpdateCount());
                return;
            }

            try (ResultSet rs = statement.getResultSet()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columnCount = meta.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) System.out.print("\t");
                    System.out.print(escape(meta.getColumnLabel(i)));
                }
                System.out.println();

                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    for (int i = 1; i <= columnCount; i++) {
                        if (i > 1) System.out.print("\t");
                        Object value = rs.getObject(i);
                        System.out.print(value == null ? "NULL" : escape(String.valueOf(value)));
                    }
                    System.out.println();
                }
                System.err.println("ROWS\t" + rowCount);
            }
        }
    }

    private static Connection connectWithRetry(String url, String user, String password, int retryCount, int retryDelayMillis) throws Exception {
        SQLException lastException = null;
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException ex) {
                lastException = ex;
                if (attempt == retryCount || !isRetryableH2ConnectError(ex)) {
                    throw ex;
                }
                Thread.sleep((long) retryDelayMillis * attempt);
            }
        }

        throw lastException;
    }

    private static boolean isRetryableH2ConnectError(SQLException ex) {
        if (ex.getErrorCode() == 8000 || ex.getErrorCode() == 90020 || ex.getErrorCode() == 90067) {
            return true;
        }

        String message = ex.getMessage();
        return message != null
            && (message.contains("Lock file recently modified")
                || message.contains("Database may be already in use")
                || message.contains("Connection is broken"));
    }

    private static String escape(String value) {
        return value.replace("\t", " ").replace("\r", " ").replace("\n", " ");
    }
}
'@

$runnerDir = Ensure-RunnerCompiled -RunnerDir (Join-Path $cacheDir 'runner') -RunnerSource $runnerSource
$classPathSeparator = [System.IO.Path]::PathSeparator
$classPath = "$resolvedDriverJar$classPathSeparator$runnerDir"

$runId = [System.Guid]::NewGuid().ToString('N')
$outputFile = Join-Path $cacheDir "query-output-$runId.tsv"
$errorFile = Join-Path $cacheDir "query-error-$runId.txt"
& java -cp $classPath QueryJdbcRunner $JdbcUrl $User $Password $sqlToRun $MaxRows $RetryCount $RetryDelayMillis 1> $outputFile 2> $errorFile
$output = if (Test-Path -LiteralPath $outputFile) { Get-Content -LiteralPath $outputFile -Encoding UTF8 } else { @() }
$errorOutput = if (Test-Path -LiteralPath $errorFile) { Get-Content -LiteralPath $errorFile -Encoding UTF8 } else { @() }
if ($LASTEXITCODE -ne 0) {
    $output
    $errorOutput
    exit $LASTEXITCODE
}

if ($RefreshSchemaCache) {
    Write-SchemaCache -Path $schemaCachePath -Type $DbType -Url $JdbcUrl -LoginUser $User -Lines $output
}

$output
$errorOutput
Remove-Item -LiteralPath $outputFile, $errorFile -Force -ErrorAction SilentlyContinue

Write-LastSuccess -Path $lastSuccessPath -Type $DbType -Url $JdbcUrl -LoginUser $User -ResolvedDriverJar $resolvedDriverJar
