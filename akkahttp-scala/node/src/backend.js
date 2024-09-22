 //const url = "http://localhost:8080"
const url = ""

export function getBooks() {
  return fetch(`${url}/books`)
    .then(resp => {
      getPromoMessage()
      return resp.json()
    })
}

export function getBookDetails(bookId) {
  return fetch(`${url}/books/${bookId}`)
    .then(resp => {
      getPromoMessage()
      return resp.json()
    })
}

export function putCopyHold(copyId) {
  return fetch(`${url}/copies/${copyId}`, {"method": "PUT"})
    .then(resp => {
      getPromoMessage()
      return resp.json()
    })
}

/** Emulate purchase by making the copy unavailable. It will no longer be shown */
export function buyCopy(copy) {
  copy.available = false;
  return fetch(
    `${url}/copies`,
    {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(copy),
      method: "PUT"
    });
}

export function getUser() {
  return fetch(`${url}/user`).then(resp => resp.json());
}

export function setUser(name) {
  return fetch(`${url}/user/${name}`, {"method": "PUT"});
}

// Fetch the promo message. The server side implementation relies on the existence of the state request
// for the page where the promo message will be displayed. To ensure proper bootstrapping, always call this
// after the main API call with the page content has returned, which does the targeting, has returned.
async function getPromoMessage() {
    fetch(`${url}/promo`)
      .then(resp => resp.json().then(
        message => {
          console.log(message)
          const promoDiv = document.getElementById('promo')
          if (message.length > 0) {
            promoDiv.innerHTML = message
            promoDiv.style.display = "block"
          } else {
            promoDiv.style.display = "none"
          }
        }))
}
