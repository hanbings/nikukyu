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
    useDisclosure
} from "@nextui-org/react";
import {access} from '../types/token.ts'

export function Account() {
    const {isOpen, onOpen, onOpenChange} = useDisclosure();

    return (
        <div className="w-screen h-screen flex flex-col gap-3 bg-green-100 p-2 sm:p-12">
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">Yours Account</p>
                    <p className="text-sm text-gray-500">Click card to edit your account</p>
                </div>
                <div>

                </div>
                <div className="flex flex-row gap-2">
                    <Button className="bg-green-200">Log out Account</Button>
                    <Button className="bg-red-500 text-white">Delete Account</Button>
                </div>
            </div>
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">Authorized Applications</p>
                    <p className="text-sm text-gray-500">Click card to change access or delete it</p>
                </div>
            </div>
            <div className="w-full flex flex-col gap-3 rounded-2xl bg-white p-6">
                <div>
                    <p className="text-2xl">Developer Settings</p>
                    <p className="text-sm text-gray-500">Create your own application</p>
                </div>
                <div>
                    <Button className="bg-green-200" onPress={onOpen}>Apply a new OAuth Application</Button>
                </div>
            </div>
            <Modal isOpen={isOpen} onOpenChange={onOpenChange}>
                <ModalContent>
                    {(onClose) => (
                        <>
                            <ModalHeader className="flex flex-col gap-1">Apply a new OAuth Application</ModalHeader>
                            <ModalBody>
                                <Input type="text" label="Application Name"/>
                                <Input type="text" label="Application Description"/>
                                <Input type="text" label="Application Homepage"/>
                                <Input type="text" label="Private Policy"/>
                                <Input type="text" label="Teams of Use"/>
                                <Input type="text" label="Theme Color (Using HEX RGB value)"/>
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
                                                        ? <Chip className="bg-red-500"
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