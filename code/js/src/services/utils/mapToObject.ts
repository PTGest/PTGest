function mapToObject(map: Map<any, any>): any {
    console.log('mapToObject', map);
    const obj: any = {};
    map.forEach((value, key) => {
        obj[key] = value;
    });
    return obj;
}

export default mapToObject;
