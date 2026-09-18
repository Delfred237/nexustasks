// Types partagés entre features

export interface ApiError {
  type: string
  title: string
  status: number
  detail: string
  errorCode: string
  timestamp: string
  instance: string
  errors?: Array<{
    field: string
    message: string
    rejectedValue?: unknown
  }>
}

export interface Page<T> {
  content: T[]
  totalPages: number
  totalElements: number
  size: number
  number: number
  first: boolean
  last: boolean
}