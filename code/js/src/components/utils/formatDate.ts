const formattedDate = (selectedDay: Date): string => {
    const year = selectedDay.getFullYear()
    const month = selectedDay.getMonth() + 1 // getMonth() is zero-based
    const day = selectedDay.getDate()
    return `${year}-${month}-${day}`
}
export default formattedDate
