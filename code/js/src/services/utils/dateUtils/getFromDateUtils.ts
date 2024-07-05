function getDayFromDate(dateString: string): number {
    const date = new Date(dateString);
    return date.getUTCDate();
}


function getMonthFromDate(dateString: string): number {
    const date = new Date(dateString);
    return date.getUTCMonth();
}

function getYearFromDate(dateString: string): number {
    const date = new Date(dateString);
    return date.getUTCFullYear();
}

export {
    getDayFromDate,
    getMonthFromDate,
    getYearFromDate
};

