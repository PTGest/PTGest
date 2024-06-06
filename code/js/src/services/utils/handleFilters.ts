function handleFilters(inputString: string, filters:string[]): string{
    console.log(filters)
    console.log(filters.length)
    let url = inputString;
    filters.forEach((filter) => {
        url += `&${filter}`;
    });
    return url;
}
export default handleFilters;
