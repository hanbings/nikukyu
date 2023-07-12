export class Message<T> {
    constructor(
        public code: number,
        public message: string,
        public data: T,
    ) {
    }
}