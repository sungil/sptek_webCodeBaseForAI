param(
    [Parameter(Position = 0)]
    [string] $Sql,

    [string] $FilePath
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

function Resolve-H2Jar {
    $h2Root = Join-Path $env:USERPROFILE '.gradle\caches\modules-2\files-2.1\com.h2database\h2'
    if (-not (Test-Path -LiteralPath $h2Root)) {
        throw "H2 Gradle cache directory does not exist: $h2Root"
    }

    $jar = Get-ChildItem -Path $h2Root -Recurse -Filter 'h2-*.jar' |
        Where-Object { $_.Name -notlike '*sources*' -and $_.Name -notlike '*javadoc*' } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

    if (-not $jar) {
        throw "Cannot find executable H2 jar under: $h2Root"
    }

    return $jar.FullName
}

function Assert-ReadOnlySql {
    param([string] $Statement)

    $trimmed = $Statement.TrimStart()
    if ($trimmed -notmatch '^(?is)(select|show|with|explain|call\s+information_schema\.)\b') {
        throw 'Only read-only SQL is allowed by this support script.'
    }

    if ($Statement -match '(?is)\b(insert|update|delete|merge|create|alter|drop|truncate|grant|revoke|runscript|script|backup|shutdown)\b') {
        throw 'Potential write, DDL, or database control SQL was detected. This support script does not execute it.'
    }
}

if (-not $Sql -and -not $FilePath) {
    throw 'Provide either SQL text or -FilePath.'
}

if ($Sql -and $FilePath) {
    throw 'Provide only one of SQL text or -FilePath.'
}

$repoRoot = Resolve-RepoRoot
Set-Location -LiteralPath $repoRoot

$dbFile = Join-Path $repoRoot 'infra\h2DB\spt_web_fw.mv.db'
if (-not (Test-Path -LiteralPath $dbFile)) {
    throw "Local H2 DB file does not exist. Refusing to create an empty DB: $dbFile"
}

$sqlToRun = $Sql
if ($FilePath) {
    $resolvedFile = Resolve-Path -LiteralPath $FilePath
    $sqlToRun = Get-Content -LiteralPath $resolvedFile -Raw -Encoding UTF8
}

Assert-ReadOnlySql -Statement $sqlToRun

$h2Jar = Resolve-H2Jar
$jdbcUrl = 'jdbc:h2:file:./infra/h2DB/spt_web_fw;AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9092'

java -cp $h2Jar org.h2.tools.Shell `
    -url $jdbcUrl `
    -user sa `
    -password '' `
    -sql $sqlToRun
