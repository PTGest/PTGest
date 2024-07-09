export default function formatPhoneNumber(phoneNumber: string): string {
    // Regular expression to match the country code and the rest of the number
    const regex = /^(\+\d{1,3})(\d{3})(\d+)$/;
    const match = phoneNumber.match(regex);

    if (!match) {
        throw new Error("Invalid phone number format");
    }

    const countryCode = match[1];
    const firstPart = match[2];
    const secondPart = match[3];

    return `${countryCode} - ${firstPart}${secondPart}`;
}
