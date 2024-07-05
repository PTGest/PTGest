class BodyCircumferences {
    neck: number;
    chest: number;
    waist: number;
    hips: number;
    thighs: number;
    calves: number;
    relaxedRightArm: number;
    relaxedLeftArm: number;
    flexedRightArm: number;
    flexedLeftArm: number;
    forearm: number;

    constructor(
        neck: number,
        chest: number,
        waist: number,
        hips: number,
        thighs: number,
        calves: number,
        relaxedRightArm: number,
        relaxedLeftArm: number,
        flexedRightArm: number,
        flexedLeftArm: number,
        forearm: number
    ) {
        this.neck = neck;
        this.chest = chest;
        this.waist = waist;
        this.hips = hips;
        this.thighs = thighs;
        this.calves = calves;
        this.relaxedRightArm = relaxedRightArm;
        this.relaxedLeftArm = relaxedLeftArm;
        this.flexedRightArm = flexedRightArm;
        this.flexedLeftArm = flexedLeftArm;
        this.forearm = forearm;
    }
}
export default BodyCircumferences;
