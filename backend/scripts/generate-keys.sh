#!/bin/bash
# Generate RSA key pair for JWT signing (development only).
# Run once from the backend/ directory: ./scripts/generate-keys.sh
#
# WARNING: The private key MUST NOT be committed to git.
#          It is already listed in .gitignore (*.pem).
#          For production, use Docker secrets or a vault solution.
set -e

CERTS_DIR="src/main/resources/certs"

mkdir -p "$CERTS_DIR"

echo "Generating RSA 2048-bit private key..."
openssl genpkey -algorithm RSA -out "$CERTS_DIR/private.pem" -pkeyopt rsa_keygen_bits:2048

echo "Extracting public key..."
openssl rsa -pubout -in "$CERTS_DIR/private.pem" -out "$CERTS_DIR/public.pem"

echo ""
echo "Keys generated:"
echo "  Private key : $CERTS_DIR/private.pem  <-- NEVER commit this file"
echo "  Public key  : $CERTS_DIR/public.pem   <-- safe to commit for dev"
echo ""
echo "The public.pem is referenced by application.yml:"
echo "  spring.security.oauth2.resourceserver.jwt.public-key-location: classpath:certs/public.pem"
