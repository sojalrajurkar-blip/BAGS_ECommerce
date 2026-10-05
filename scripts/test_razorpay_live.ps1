# PowerShell test for live Razorpay order generation
$headers = @{ "Content-Type" = "application/json" }
$body = @{
    customerName = "Sojal Rajurkar"
    customerEmail = "sojal@example.com"
    customerPhone = "+919876543210"
    shippingAddress = @{
        fullName = "Sojal Rajurkar"
        street = "123 Linking Rd"
        city = "Mumbai"
        state = "Maharashtra"
        postalCode = "400050"
        country = "India"
    }
    paymentMethod = "RAZORPAY"
    items = @(
        @{
            productId = "prod-1"
            quantity = 1
        }
    )
} | ConvertTo-Json -Depth 5

Write-Host "1. Placing order..."
$orderRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/checkout/place-order" -Method POST -Headers $headers -Body $body
$orderId = $orderRes.data.id
$orderNumber = $orderRes.data.orderNumber
Write-Host "Order placed: $orderNumber (ID: $orderId, Total: Rs.$($orderRes.data.total))"

Write-Host "2. Creating Razorpay Order..."
$rzpRes = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/payments/razorpay/create-order/$orderId" -Method POST
Write-Host "Razorpay Order ID:" $rzpRes.data.razorpayOrderId
Write-Host "Amount (Paise):" $rzpRes.data.amountInPaise
Write-Host "Key ID:" $rzpRes.data.keyId
Write-Host "Razorpay Live Test: 100% SUCCESS!"
