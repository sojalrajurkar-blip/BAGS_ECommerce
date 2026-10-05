$env:DB_URL = "jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?sslmode=require"
$env:DB_USERNAME = "postgres.ullttserqcgifmoklcpx"
$env:DB_PASSWORD = "i8b4pFMXZFnw6uqf"
$env:SPRING_PROFILES_ACTIVE = "dev"

Write-Host "Connecting to Supabase PostgreSQL at aws-0-ap-southeast-1.pooler.supabase.com..."
mvn spring-boot:run
