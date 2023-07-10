import {AccessType, AccountLogType} from "./common.ts";

export class Account {
    constructor(
        public auid: string,
        public created: number,
        public verified: boolean,
        public id: string,
        public nick: string,
        public avatar: string,
        public background: string,
        public color: string,
        public email: string,
    ) {
    }
}

export class AccountAuthorization {
    constructor(
        public aaid: string,
        public created: number,
        public auid: string,
        public provider: string,
        public openid: string,
    ) {
    }
}

export class AccountLog {
    constructor(
        public alid: string,
        public created: number,
        public auid: string,
        public ip: string,
        public type: AccountLogType,
    ) {
    }
}

export class AccountOAuth {
    constructor(
        public aoid: string,
        public created: number,
        public auid: string,
        public ouid: string,
        public access: Array<AccessType>,
    ) {
    }
}

