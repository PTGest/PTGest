// function getRandomIcon(): string {
//     const icons: string[] = ["boy.png", "girl.png", "man.png", "man2.png", "man3.png", "user.png", "user2.png", "who.png", "woman.png", "woman2.png"]
//
//     const randomIndex = Math.floor(Math.random() * icons.length)
//     return icons[randomIndex]
// }

// Example usage

const apiBaseUri = import.meta.env.VITE_API_URI
const vueBaseUri = import.meta.env.VITE_VUE_PORT

export { apiBaseUri, vueBaseUri }
