$custLoginBody = @{
    email = "customer@rora.com"
    password = "CustomerPassword123!"
} | ConvertTo-Json
$custLoginRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/auth/login" -Method Post -Body $custLoginBody -ContentType "application/json"
$custToken = $custLoginRes.data.accessToken
$custHeaders = @{
    "Authorization" = "Bearer $custToken"
    "Content-Type"  = "application/json"
}

# Fetch active products
$prods = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/products" -Method Get
$firstProd = $prods.data.content[0]
Write-Host "Product to order: ID=$($firstProd.id), Title=$($firstProd.title), Price=$($firstProd.price)"

$checkoutBody = @{
    items = @(
        @{
            productId = $firstProd.id
            quantity = 1
        }
    )
    shippingAddress = @{
        recipientName = "Test Client"
        phone = "+919876543210"
        addressLine1 = "100 Luxury Avenue"
        city = "Mumbai"
        state = "Maharashtra"
        postalCode = "400001"
        country = "India"
    }
    billingAddress = @{
        recipientName = "Test Client"
        phone = "+919876543210"
        addressLine1 = "100 Luxury Avenue"
        city = "Mumbai"
        state = "Maharashtra"
        postalCode = "400001"
        country = "India"
    }
    paymentMethod = "CARD"
} | ConvertTo-Json

try {
    $orderRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/checkout" -Method Post -Headers $custHeaders -Body $checkoutBody
    Write-Host "Success! Order ID=$($orderRes.data.id), Number=$($orderRes.data.orderNumber)"
} catch {
    Write-Host "Error status: $($_.Exception.Response.StatusCode.value__)"
    $stream = $_.Exception.Response.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $responseBody = $reader.ReadToEnd()
    Write-Host "Response Body: $responseBody"
}
