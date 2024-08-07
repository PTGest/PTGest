<template>
    <div class="wrapper">
        <header>
            <p class="current-date">{{ currDateString }}</p>
            <div class="icons">
                <font-awesome-icon class="icon" @click="handleIconClick('prev')" id="prev" :icon="faChevronLeft">chevron_left</font-awesome-icon>
                <font-awesome-icon class="icon" @click="handleIconClick('next')" id="next" :icon="faChevronRight">chevron_right</font-awesome-icon>
            </div>
        </header>
        <div class="calendar">
            <ul class="weeks">
                <li>Sun</li>
                <li>Mon</li>
                <li>Tue</li>
                <li>Wed</li>
                <li>Thu</li>
                <li>Fri</li>
                <li>Sat</li>
            </ul>
            <ul class="days">
                <li v-bind="day" @click="getDate(day)" v-for="day in getDaysInMonth()" :class="day.class">{{ day.day }}</li>
            </ul>
        </div>
    </div>
</template>

<script setup lang="ts">
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome"
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons"
import { ref } from "vue"
import Day from "../calendar/Day.ts"
import TrainerSession from "@/views/user/trainerViews/models/sessions/TrainerSession.ts"
import { getDayFromDate, getMonthFromDate } from "@/services/utils/dateUtils/getFromDateUtils.ts"

const emits = defineEmits(["getDate"])

const props = defineProps<{
    trainDays: string[]
}>()

const trainDays = ref(
    props.trainDays.map((day: string) => ({
        day: getDayFromDate(day),
        month: getMonthFromDate(day),
    }))
)

// daysTag = document.getElementById("days"),
const icons = document.querySelectorAll(".icon")

// getting new date, current year and month
let date = new Date(),
    currYear = date.getFullYear(),
    currMonth = date.getMonth(),
    currDay = date.getDate()

const currDateString = ref("")
const selectedDay = ref("")
// storing full name of all months in array
const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]

//Get days
const getDaysInMonth = () => {
    const firstDayofMonth = new Date(currYear, currMonth, 1).getDay(), // getting first day of month
        lastDayofMonth = new Date(currYear, currMonth + 1, 0).getDate(), // getting last day of month
        lastDayofNextMonth = new Date(currYear, currMonth, lastDayofMonth).getDay(), // getting last day of month
        lastDateofLastMonth = new Date(currYear, currMonth, 0).getDate() // getting last date of previous month

    const currentMonthDays = [] // creating an empty array for current month days
    //lastMonthDays = []; // creating an empty array for last month days
    for (let i = firstDayofMonth; i > 0; i--) {
        // loop through all days of last month
        currentMonthDays.push(new Day(`${lastDateofLastMonth - i + 1}`, "inactive")) // push all days in last month days array
    }
    for (let i = 1; i <= lastDayofMonth; i++) {
        let classString = "normal"
        // loop through all days of current month
        if (i === new Date().getDate() && currMonth === new Date().getMonth()) {
            classString = trainDays.value.find((day) => day.day == i) ? "active-train" : "active"
        } else if (trainDays.value.find((day) => day.day == i && currMonth == day.month) && currMonth === new Date().getMonth()) {
            classString = "train"
        }
        currentMonthDays.push(new Day(`${i}`, classString))
    }

    for (let i = 1; i <= 6 - lastDayofNextMonth; i++) {
        // loop through all days of next month
        currentMonthDays.push(new Day(`${i}`, "inactive")) // push all days in next month days array
    }

    currDateString.value = `${months[currMonth]} ${currYear}` // setting current month and year in current date string
    return currentMonthDays
}

const handleIconClick = (action: string) => {
    if (action === "prev") {
        currMonth = currMonth - 1
    } else {
        currMonth = currMonth + 1
    }
    if (currMonth < 0 || currMonth > 11) {
        // if current month is less than 0 or greater than 11
        // creating a new date of current year & month and pass it as date value
        date = new Date(currYear, currMonth, new Date().getDate())
        currYear = date.getFullYear() // updating current year with new date year
        currMonth = date.getMonth() // updating current month with new date month
    } else {
        date = new Date() // pass the current date as date value
    }
    getDaysInMonth()
}

icons.forEach((icon) => {
    // getting prev and next icons
    icon.addEventListener("click", () => {
        // adding click event on both icons
        // if clicked icon is previous icon then decrement current month by 1 else increment it by 1
        currMonth = icon.id === "prev" ? currMonth - 1 : currMonth + 1
    })
})

const getDate = (day: Day) => {
    const monthIndex = months.findIndex((month) => month === currDateString.value.split(" ")[0]) + 1
    const formattedDate = `${currYear}-${String(monthIndex).padStart(2, "0")}-${String(day.day).padStart(2, "0")}`
    selectedDay.value = day.day
    emits("getDate", formattedDate)
}
</script>

<style scoped>
.wrapper {
    width: 450px;
    background: var(--main-primary-color);
    border-radius: 10px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.12);
}
.wrapper header {
    display: flex;
    align-items: center;
    padding: 25px 30px 10px;
    justify-content: space-between;
    color: whitesmoke;
}

.icon {
    padding: 0.5em;
    cursor: pointer;
    width: 1.5em;
    height: 1.5em;
    color: whitesmoke;
    border-radius: 50%;
}
.icon:hover {
    background: var(--sign-up-blue);
}

header .current-date {
    font-size: 1.45rem;
    font-weight: 500;
}
.calendar {
    padding: 20px;
}
.calendar ul {
    display: flex;
    flex-wrap: wrap;
    list-style: none;
    text-align: center;
}
.calendar .days {
    margin-bottom: 20px;
}
.calendar li {
    color: whitesmoke;
    width: calc(100% / 7);
    font-size: 1.07rem;
}
.calendar .weeks li {
    font-weight: 500;
    cursor: default;
}
.calendar .days li {
    z-index: 1;
    cursor: pointer;
    position: relative;
    margin-top: 30px;
}
.days li.inactive {
    color: var(--main-tertiary-color);
}
.days li.active {
    color: whitesmoke;
}

.days li::before {
    position: absolute;
    content: "";
    left: 50%;
    top: 50%;
    height: 40px;
    width: 40px;
    z-index: -1;
    border-radius: 50%;
    transform: translate(-50%, -50%);
}

.days li.active::before {
    background: var(--sign-up-blue);
}
.days li:not(.active):hover::before {
    background: var(--main-secondary-color);
}
.days li:not(.active):hover {
    color: whitesmoke;
}

.days li.train::before {
    background: red;
}

.days li.active-train::before {
    background: var(--sign-up-blue);
    border: 2px solid red;
}

ul {
    padding: 0;
    margin: 0;
}
</style>
