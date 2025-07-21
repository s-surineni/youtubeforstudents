<#
.SYNOPSIS
    Kotlin linting and formatting script for Android project
.DESCRIPTION
    This script provides commands to check, format, and apply ktlint rules to Kotlin files
.PARAMETER Action
    The action to perform: check, format, or apply
.EXAMPLE
    .\lint.ps1 -Action check
    .\lint.ps1 -Action format
    .\lint.ps1 -Action apply
.NOTES
    Author: Your Name
    Date: $(Get-Date)
    Version: 1.0
#>

[CmdletBinding()]
param(
    [Parameter(Mandatory = $true, Position = 0)]
    [ValidateSet('check', 'format', 'apply')]
    [string]$Action
)

# Function to run ktlint command
function Invoke-Ktlint {
    param(
        [string]$Command
    )
    
    Write-Host "Running: $Command" -ForegroundColor Green
    $result = Invoke-Expression $Command
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ ktlint $Command completed successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ ktlint $Command failed with exit code $LASTEXITCODE" -ForegroundColor Red
    }
    
    return $result
}

# Main script logic
switch ($Action) {
    'check' {
        Write-Host "🔍 Checking Kotlin code style..." -ForegroundColor Yellow
        Invoke-Ktlint ".\gradlew ktlintCheck"
    }
    'format' {
        Write-Host "🎨 Formatting Kotlin code..." -ForegroundColor Yellow
        Invoke-Ktlint ".\gradlew ktlintFormat"
    }
    'apply' {
        Write-Host "🔍 Checking code style..." -ForegroundColor Yellow
        Invoke-Ktlint ".\gradlew ktlintCheck"
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ All files pass ktlint checks!" -ForegroundColor Green
        } else {
            Write-Host "⚠️  Some files need formatting. Running format..." -ForegroundColor Yellow
            Invoke-Ktlint ".\gradlew ktlintFormat"
            
            Write-Host "🔍 Re-checking after formatting..." -ForegroundColor Yellow
            Invoke-Ktlint ".\gradlew ktlintCheck"
        }
    }
}

Write-Host "`n📝 Available commands:" -ForegroundColor Cyan
Write-Host "  .\lint.ps1 check   - Check code style without making changes" -ForegroundColor White
Write-Host "  .\lint.ps1 format  - Format code to match style rules" -ForegroundColor White
Write-Host "  .\lint.ps1 apply   - Check and format if needed" -ForegroundColor White 