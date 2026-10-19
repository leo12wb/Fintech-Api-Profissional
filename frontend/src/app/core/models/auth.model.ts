export interface LoginRequest { loginId: string; password: string; }
export interface RegisterRequest { name: string; email: string; loginId: string; password: string; }
export interface AuthResponse {
  accessToken: string; refreshToken: string; tokenType: string;
  expiresIn: number; customerId: string; email: string; role: string;
}
export interface DecodedToken { sub: string; email: string; roles: string[]; exp: number; iat: number; }
