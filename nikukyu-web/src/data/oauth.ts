import {AccessType, OAuthLogType} from "./common.ts";

export class OAuth {
    constructor(
        public ouid: string,
        public created: number,
        public redirect: Array<string>,
        public access: Array<AccessType>,
        public avatar: string,
        public name: string,
        public description: string,
        public homepage: string,
        public background: string,
        public theme: string,
        public policy: string,
        public tos: string,
    ) {
    }
}

export class OAuthClient {
    constructor(
        public ocid: string,
        public created: number,
        public ouid: string,
        public secret: string,
        public expire: number,
    ) {
    }
}

export class OAuthLog {
    constructor(
        public olid: string,
        public created: number,
        public ouid: string,
        public type: OAuthLogType,
    ) {
    }
}

