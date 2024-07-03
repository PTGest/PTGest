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
        this.requirePositive(neck, "Neck circumference must be a positive number");
        this.requirePositive(chest, "Chest circumference must be a positive number");
        this.requirePositive(waist, "Waist circumference must be a positive number");
        this.requirePositive(hips, "Hips circumference must be a positive number");
        this.requirePositive(thighs, "Thighs circumference must be a positive number");
        this.requirePositive(calves, "Calves circumference must be a positive number");
        this.requirePositive(relaxedRightArm, "Relaxed right arm circumference must be a positive number");
        this.requirePositive(relaxedLeftArm, "Relaxed left arm circumference must be a positive number");
        this.requirePositive(flexedRightArm, "Flexed right arm circumference must be a positive number");
        this.requirePositive(flexedLeftArm, "Flexed left arm circumference must be a positive number");
        this.requirePositive(forearm, "Forearm circumference must be a positive number");

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

    private requirePositive(value: number, errorMessage: string): void {
        if (value <= 0) {
            throw new Error(errorMessage);
        }
    }
}
export default BodyCircumferences;
