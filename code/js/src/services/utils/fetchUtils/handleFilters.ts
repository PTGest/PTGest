function handleFilters(inputString: string, filters: Map<string, any>): string {
    let url = inputString
    const filterEntries = Array.from(filters.entries())

    if (filterEntries.length > 0) {
        url += "?"
        url += filterEntries.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`).join("&")
    }

    return url
}

export default handleFilters
