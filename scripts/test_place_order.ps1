$baseUrl = "http://localhost:8080/api/v1"

# 1. Admin login
$adminLoginBody = @{ email = "admin@rora-luxury.com"; password = "Password123!" } | ConvertTo-Json
$loginRes = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $adminLoginBody -ContentType "application/json"
$token = $loginRes.data.accessToken
$headers = @{ "Authorization" = "Bearer $token"; "Content-Type" = "application/json" }

# 2. Get active product
$prods = Invoke-RestMethod -Uri "$baseUrl/products" -Method Get
$p = $prods.data.content[0]
Write-Host "Product ID: $($p.id), SKU: $($p.sku), Price: $($p.price)"

# 3. Customer registration
$email = "test." + (Get-Random) + "@example.com"
$regBody = @{ name = "Tester"; email = $email; password = "Password123!" } | ConvertTo-Json
$regRes = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $regBody -ContentType "application/json"
$custToken = $regRes.data.accessToken
$custHeaders = @{ "Authorization" = "Bearer $custToken"; "Content-Type" = "application/json" }
Write-Host "Registered: $email"

# 4. Checkout
$checkoutBody = @{
    customerName = "Tester"
    customerEmail = $email
    customerPhone = "+919876543210"
    items = @(
        @{
            productId = $p.id
            quantity = 1
        }
    )
    shippingAddress = @{
        fullName = "Tester"
        street = "100 Luxury Avenue"
        city = "Mumbai"
        state = "Maharashtra"
        postalCode = "400001"
        country = "India"
        phone = "+919876543210"
    }
    billingAddress = @{
        fullName = "Tester"
        street = "100 Luxury Avenue"
        city = "Mumbai"
        state = "Maharashtra"
        postalCode = "400001"
        country = "India"
        phone = "+919876543210"
    }
    paymentMethod = "CARD"
} | ConvertTo-Json

try {
    $orderRes = Invoke-RestMethod -Uri "$baseUrl/checkout/place-order" -Method Post -Headers $custHeaders -Body $checkoutBody
    Write-Host "ORDER SUCCESS! ID: $($orderRes.data.id), Number: $($orderRes.data.orderNumber)"
} catch {
    Write-Host "Order Error: $($_.Exception.Message)"
    if ($_.Exception.Response) {
        $stream = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($stream)
        Write-Host "Response Body: $($reader.ReadToEnd())"
    }
}
