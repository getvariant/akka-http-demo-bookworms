 //const url = "http://localhost:8080"
const url = ""

export function getBooks() {
  return fetch(
    `${url}/books`).then(resp => resp.json());
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

export async function getPromoMessage() {
  var retries = 0
  while(retries < 100) {
    const resp = await fetch(`${url}/promo`)
    if (resp.status == 200) return resp.json()
    else if (resp.status == 204) retries++
    else throw new Error("Unexpected response " + resp.status)
  }
  // If after this many retries we haven't gotten an OK response,
  // we're assuming to have been disqualified.
  return ""
}
