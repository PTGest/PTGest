function handleFilters(inputString: string, filters: Map<string, any>): string {
    console.log(filters)
    let url = inputString
    const filterEntries = Array.from(filters.entries())

    if (filterEntries.length > 0) {
        url += "?"
        url += filterEntries.map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`).join("&")
    }

    console.log("url", url)
    return url
}

export default handleFilters
