$adminLoginBody = @{ email = "admin@rora-luxury.com"; password = "Password123!" } | ConvertTo-Json
$loginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Body $adminLoginBody -ContentType "application/json"
$adminToken = $loginRes.data.token
Write-Host "User: $($loginRes.data.user.email), Roles: $($loginRes.data.user.roles -join ',')"
Write-Host "Token: $($adminToken.Substring(0, 30))..."

$headers = @{
    "Authorization" = "Bearer $adminToken"
    "Content-Type"  = "application/json"
}

$users = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/admin/users" -Method Get -Headers $headers
Write-Host "Admin Users SUCCESS: Total Elements = $($users.data.totalElements)"

$dash = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/admin/dashboard/stats" -Method Get -Headers $headers
Write-Host "Dashboard stats SUCCESS: Total Revenue = Rs.$($dash.data.totalRevenue)"
