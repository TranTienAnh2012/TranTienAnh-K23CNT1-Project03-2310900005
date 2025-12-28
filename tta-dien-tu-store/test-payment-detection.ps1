# Diagnostic Script - Test Payment Detection (MINIMAL API CALLS)
# This script will make ONLY ONE call to test the payment flow

Write-Host "=== Payment Detection Diagnostic ===" -ForegroundColor Cyan
Write-Host "WARNING: This will make API calls to VPBank. Use carefully!" -ForegroundColor Yellow
Write-Host ""

# Step 1: Check Flask API health
Write-Host "Step 1: Checking Flask API..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:5000/api/health" -Method Get -ErrorAction Stop
    Write-Host "   ✓ Flask API: $($health.status)" -ForegroundColor Green
    Write-Host "   ✓ Authenticated: $($health.authenticated)" -ForegroundColor Green
    
    if (-not $health.authenticated) {
        Write-Host "   ✗ Flask is not authenticated with VPBank!" -ForegroundColor Red
        Write-Host "   Please check Flask terminal for OTP prompt" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "   ✗ Cannot connect to Flask API" -ForegroundColor Red
    Write-Host "   Make sure Flask is running: python 1764132999-VP_Bank_Check_Lsgd.py" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Step 2: Enter payment details to test" -ForegroundColor Yellow
Write-Host "   (This should match a REAL transfer you just made)" -ForegroundColor Gray
Write-Host ""

# Get test parameters
$amount = Read-Host "   Enter amount (VND)"
$sessionId = Read-Host "   Enter session ID (e.g., 20)"
$content = "TTA$sessionId"

Write-Host ""
Write-Host "Step 3: Testing payment detection..." -ForegroundColor Yellow
Write-Host "   Amount: $amount VND" -ForegroundColor Gray
Write-Host "   Content: $content" -ForegroundColor Gray
Write-Host "   URL: http://localhost:5000/api/check-payment?amount=$amount&content=$content" -ForegroundColor Gray
Write-Host ""

# Make the API call
try {
    $encodedContent = [System.Uri]::EscapeDataString($content)
    $url = "http://localhost:5000/api/check-payment?amount=$amount&content=$encodedContent"
    
    Write-Host "   Calling Flask API..." -ForegroundColor Gray
    $result = Invoke-RestMethod -Uri $url -Method Get -ErrorAction Stop
    
    Write-Host ""
    Write-Host "=== RESULT ===" -ForegroundColor Cyan
    Write-Host "   Success: $($result.success)" -ForegroundColor $(if ($result.success) { "Green" } else { "Red" })
    Write-Host "   Found: $($result.found)" -ForegroundColor $(if ($result.found) { "Green" } else { "Yellow" })
    
    if ($result.found) {
        Write-Host ""
        Write-Host "   ✓ PAYMENT DETECTED!" -ForegroundColor Green
        Write-Host "   Transaction Details:" -ForegroundColor Cyan
        Write-Host "     - Amount: $($result.transaction.amount) VND" -ForegroundColor White
        Write-Host "     - Description: $($result.transaction.description)" -ForegroundColor White
        Write-Host "     - Date: $($result.transaction.date)" -ForegroundColor White
        Write-Host "     - Reference: $($result.transaction.reference)" -ForegroundColor White
    } else {
        Write-Host ""
        Write-Host "   ✗ Payment not found" -ForegroundColor Yellow
        Write-Host "   Message: $($result.message)" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "   Possible reasons:" -ForegroundColor Yellow
        Write-Host "   1. Transfer not completed yet" -ForegroundColor Gray
        Write-Host "   2. Amount doesn't match exactly" -ForegroundColor Gray
        Write-Host "   3. Content (TTA$sessionId) doesn't match transfer description" -ForegroundColor Gray
        Write-Host "   4. VPBank API rate limit (check Flask logs)" -ForegroundColor Gray
    }
    
    if ($result.error) {
        Write-Host ""
        Write-Host "   Error: $($result.error)" -ForegroundColor Red
    }
    
} catch {
    Write-Host ""
    Write-Host "   ✗ API call failed!" -ForegroundColor Red
    Write-Host "   Error: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "   Check Flask terminal for detailed error logs" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Diagnostic Complete ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "1. Check Flask terminal for detailed logs" -ForegroundColor White
Write-Host "2. If rate limit error, wait 1 hour before testing again" -ForegroundColor White
Write-Host "3. If payment found, Java application should work correctly" -ForegroundColor White
