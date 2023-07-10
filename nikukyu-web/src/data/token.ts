import {AccessType} from "./common.ts";

export class Token {
    constructor(
        public token: string,
        public belong: string,
        public time: number,
        public expire: number,
        public access: Array<AccessType>,
    ) {
    }
}