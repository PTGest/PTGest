function mapToObject(map: Map<any, any>): any {
    const obj: any = {}
    map.forEach((value, key) => {
        obj[key] = value
    })
    return obj
}

export default mapToObject
