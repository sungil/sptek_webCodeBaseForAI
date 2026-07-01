param(
    [Parameter(Position = 0)]
    [string] $Sql,

    [string] $FilePath,

    [switch] $AllowWrite
)

$ErrorActionPreference = 'Stop'

$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..\..\..\..')).Path
$commonScript = Join-Path $repoRoot '.AI\workflows\scripts\query-local-h2.ps1'

if (-not (Test-Path -LiteralPath $commonScript)) {
    throw "Common workflow script does not exist: $commonScript"
}

if ($FilePath) {
    & $commonScript -FilePath $FilePath -AllowWrite:$AllowWrite
}
else {
    & $commonScript $Sql -AllowWrite:$AllowWrite
}
