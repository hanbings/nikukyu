export interface Message<T> {
    message: string
    code: number
    data: T
}