export interface Message<T> {
    traceId: string
    code: number
    message: string
    timestamp: number
    data: T
}