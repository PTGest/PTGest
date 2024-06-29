export default function getDayFromDate(dateString: string): number {
    const date = new Date(dateString);
    return date.getUTCDate();
}
