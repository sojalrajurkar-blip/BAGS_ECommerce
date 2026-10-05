$emails = @("admin@rora.com", "admin@rora-luxury.com", "sarah.j@rorastudios.com")
$passwords = @("AdminPassword123!", "Password123!", "Password123!")

for ($i = 0; $i -lt $emails.Length; $i++) {
    $email = $emails[$i]
    $pwd = $passwords[$i]
    $body = @{ email = $email; password = $pwd } | ConvertTo-Json
    try {
        $res = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Body $body -ContentType "application/json"
        Write-Host "SUCCESS for $email -> Roles: $($res.data.user.roles -join ', ')" -ForegroundColor Green
    } catch {
        Write-Host "FAILED for $email ($pwd): $($_.Exception.Message)" -ForegroundColor Red
    }
}
