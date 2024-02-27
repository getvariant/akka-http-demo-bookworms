 //const url = "http://localhost:8080"
const url = ""

export function getBooks() {
  return fetch(`${url}/books`)
    .then(resp => {
      getPromoMessage();
      return resp.json()
      }
    );
}

export function getBookDetails(bookId) {
  return fetch(`${url}/books/${bookId}`).then(resp => resp.json());
}

export function putCopyHold(copyId) {
  return fetch(`${url}/copies/${copyId}`, {"method": "PUT"}).then(resp => resp.json());
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


async function getPromoMessage() {
    const resp = await fetch(`${url}/promo`)
    const text = await resp.text()
    console.log(text)
    const promoDiv = document.getElementById('promo')
    if (text.length > 0) {
      promoDiv.innerHTML = text
      promoDiv.style.display = "block"
    } else {
      promoDiv.style.display = "none"
    }
}
