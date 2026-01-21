# PowerShell script to download gradle-wrapper.jar
# This script downloads the Gradle wrapper JAR file if it's missing

$wrapperDir = "gradle\wrapper"
$wrapperJar = "$wrapperDir\gradle-wrapper.jar"
$wrapperJarUrl = "https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar"

Write-Host "Checking for gradle-wrapper.jar..." -ForegroundColor Cyan

if (Test-Path $wrapperJar) {
    Write-Host "gradle-wrapper.jar already exists." -ForegroundColor Green
    exit 0
}

Write-Host "gradle-wrapper.jar not found. Downloading..." -ForegroundColor Yellow

# Create directory if it doesn't exist
if (-not (Test-Path $wrapperDir)) {
    New-Item -ItemType Directory -Path $wrapperDir -Force | Out-Null
    Write-Host "Created directory: $wrapperDir" -ForegroundColor Gray
}

try {
    # Enable TLS 1.2 for older PowerShell versions
    [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
    
    # Download the file
    Invoke-WebRequest -Uri $wrapperJarUrl -OutFile $wrapperJar -UseBasicParsing
    
    if (Test-Path $wrapperJar) {
        Write-Host "Successfully downloaded gradle-wrapper.jar!" -ForegroundColor Green
        exit 0
    } else {
        Write-Host "ERROR: Download failed. File not found after download attempt." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "ERROR: Failed to download gradle-wrapper.jar" -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    Write-Host "`nPlease download it manually from:" -ForegroundColor Yellow
    Write-Host $wrapperJarUrl -ForegroundColor Cyan
    Write-Host "And place it at: $wrapperJar" -ForegroundColor Yellow
    exit 1
}
