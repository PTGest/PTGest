import { parseISO, format } from "date-fns"
function DateFormatter(originalDate: string) {
    const formattedDate = parseISO(originalDate)
    return format(formattedDate, "MMMM dd yyyy HH:mm")
}

export default DateFormatter
