function getRandomIcon(): string {
    let icons: string[] = ["boy.png", "girl.png", "man.png", "man2.png", "man3.png", "user.png", "user2.png", "who.png", "woman.png", "woman2.png"]

    const randomIndex = Math.floor(Math.random() * icons.length)
    return icons[randomIndex]
}

// Example usage

export default getRandomIcon
