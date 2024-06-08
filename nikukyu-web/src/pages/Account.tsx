import {
    Button,
    Chip,
    Input,
    Modal,
    ModalBody,
    ModalContent,
    ModalFooter,
    ModalHeader,
    Select,
    SelectedItems,
    SelectItem,
    Spinner,
    Table,
    TableBody,
    TableColumn,
    TableHeader,
    Textarea,
    useDisclosure,
    User,
} from "@nextui-org/react"
import {access} from '../types/token.ts'
import {Fragment, useState} from "react"
import {Account, AccountAuthorization, AccountLog, AccountOAuth} from "../data/account.ts"
import {listAccountAuthorization, listAccountLog, listAccountOAuth} from "../services/account.service.ts"
import {useQuery} from "@tanstack/react-query"
import {Message} from "../data/message.ts"
import {OAuth} from "../data/oauth.ts"
import {listOAuth} from "../services/oauth.service.ts"

export function AccountPage() {
    const [account] = useState<Account>({
        accountId: "5183509a-534a-dd65-0be1-7a0df9eeb0fe",
        created: 1717656603,
        verified: false,
        username: "example",
        nickname: "example",
        avatar: "github-mark.svg",
        email: "example@example.com",
        background: "",
        color: ""
    })

    // page content
    const {isOpen, onOpen, onOpenChange} = useDisclosure()

    return (
        <div className="min-w-full min-h-screen flex flex-col gap-3 bg-green-100 px-2 p-10 sm:p-12">
            {AccountView(account)}
            {AccountAuthorizations(account.accountId)}
            {AccountLogs(account.accountId)}
            {AccountOAuths(account.accountId)}
            {OAuths(onOpen)}
            <Modal isOpen={isOpen} onOpenChange={onOpenChange}>
                <ModalContent>
                    {(onClose) => (
                        <>
                            <ModalHeader className="flex flex-col gap-1">Apply a new OAuth Application</ModalHeader>
                            <ModalBody>
                                <Input type="text" label="Application Name"/>
                                <Input type="text" label="Application Description"/>
                                <Input type="text" label="Private Policy"/>
                                <Input type="text" label="Teams of Use"/>
                                <Input type="text" label="Theme Color (Using HEX RGB value)"/>
                                <Textarea
                                    isRequired
                                    placeholder="Application Homepage"
                                />
                                <Select
                                    items={access}
                                    label="Apply some access"
                                    variant="bordered"
                                    isMultiline={true}
                                    selectionMode="multiple"
                                    placeholder="Select a user"
                                    labelPlacement="outside"
                                    classNames={{
                                        trigger: "min-h-unit-12 py-2",
                                    }}
                                    renderValue={(items: SelectedItems<{
                                        access: string,
                                        description: string,
                                        isDanger: boolean
                                    }>) => {
                                        return (
                                            <div className="flex flex-wrap gap-2">
                                                {items.map((item) => (
                                                    item.data?.isDanger
                                                        ? <Chip className="bg-yellow-300"
                                                                key={item.key}>{item.data?.access}</Chip>
                                                        : <Chip className="bg-green-200"
                                                                key={item.key}>{item.data?.access}</Chip>
                                                ))}
                                            </div>
                                        );
                                    }}
                                >
                                    {(item) => (
                                        <SelectItem key={item.access} textValue={item.access}>
                                            <div className="flex gap-2 items-center">
                                                <div className="flex flex-col">
                                                    <span className="text-small">{item.access}</span>
                                                    <span
                                                        className="text-tiny text-default-400">{item.description}</span>
                                                </div>
                                            </div>
                                        </SelectItem>
                                    )}
                                </Select>
                            </ModalBody>
                            <ModalFooter>
                                <Button color="danger" variant="light" onPress={onClose}>
                                    Cancel
                                </Button>
                                <Button className="bg-green-200" onPress={onClose}>
                                    Done
                                </Button>
                            </ModalFooter>
                        </>
                    )}
                </ModalContent>
            </Modal>
        </div>
    )
}

function AccountView(account: Account) {
    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <div>
                <p className="text-2xl">Yours Account</p>
                <p className="text-sm text-gray-500">Click card to edit your account</p>
            </div>
            <div className="p-6 bg-gray-50 rounded-2xl">
                <User
                    name={account.nickname}
                    description={`${account.username} - ${account.email}`}
                    avatarProps={{
                        src: account.avatar,
                        size: "lg",
                    }}
                />
            </div>
            <div className="flex flex-row gap-2">
                <Button className="bg-green-200">Log out Account</Button>
                <Button className="bg-red-500 text-white">Delete Account</Button>
            </div>
        </div>
    )
}

function AccountAuthorizations(accountId: string) {
    const {isPending, data} = useQuery<Message<AccountAuthorization[]>>({
        queryKey: ['authorizations'],
        queryFn: () => listAccountAuthorization(accountId, 1, 10)
    })

    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <div>
                <p className="text-2xl">Linked Accounts</p>
                <p className="text-sm text-gray-500">View your linked accounts</p>
            </div>
            {
                isPending ?
                    <Spinner className="w-full h-[120px]" color="success" labelColor="success"/> :
                    data?.data.map(() => <Fragment/>)
            }
        </div>
    )
}

function AccountLogs(accountId: string) {
    useQuery<Message<AccountLog[]>>({
        queryKey: ['logs'],
        queryFn: () => listAccountLog(accountId, 1, 10)
    })

    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <div>
                <p className="text-2xl">Account Activity</p>
                <p className="text-sm text-gray-500">Here are recent account activity, including logins, password
                    changes, and approval of new OAuth applications.</p>
            </div>
            <div className="bg-gray-50">
                <Table aria-label="Example empty table" shadow="none">
                    <TableHeader>
                        <TableColumn>Time</TableColumn>
                        <TableColumn>IP</TableColumn>
                        <TableColumn>ACTIVITY</TableColumn>
                        <TableColumn>TYPE</TableColumn>
                    </TableHeader>
                    <TableBody emptyContent={"There are no activities."}>{[]}</TableBody>
                </Table>
            </div>
        </div>
    )
}

function AccountOAuths(accountId: string) {
    const {isPending, data} = useQuery<Message<AccountOAuth[]>>({
        queryKey: ['approve'],
        queryFn: () => listAccountOAuth(accountId, 1, 10)
    })

    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <div>
                <p className="text-2xl">Authorized Applications</p>
                <p className="text-sm text-gray-500">Click card to change access or delete it.</p>
            </div>
            {
                isPending ?
                    <Spinner className="w-full h-[120px]" color="success" labelColor="success"/> :
                    data?.data.map(() => <Fragment/>)
            }
        </div>
    )
}

function OAuths(onOpen: () => void) {
    const {isPending, data} = useQuery<Message<OAuth[]>>({
        queryKey: ['oauth'],
        queryFn: () => listOAuth(1, 10)
    })

    return (
        <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
            <div>
                <p className="text-2xl">Developer Settings</p>
                <p className="text-sm text-gray-500">Create your own application</p>
            </div>
            <div>
                <Button className="bg-green-200" onPress={onOpen}>Apply a new OAuth Application</Button>
            </div>
            {
                isPending ?
                    <Spinner className="w-full h-[120px]" color="success" labelColor="success"/> :
                    data?.data.map(() => <Fragment/>)
            }
        </div>
    )
}