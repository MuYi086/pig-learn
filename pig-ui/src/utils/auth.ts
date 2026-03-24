import Cookies from 'js-cookie'

const TokenKey = 'pig-access-token'
const RefreshTokenKey = 'pig-refresh-token'

export function getToken(): string | undefined {
  return Cookies.get(TokenKey)
}

export function setToken(token: string) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getRefreshToken(): string | undefined {
  return Cookies.get(RefreshTokenKey)
}


export function setRefreshToken(token: string) {
  return Cookies.set(RefreshTokenKey, token)
}

export function removeRefreshToken() {
  return Cookies.remove(RefreshTokenKey)
}
