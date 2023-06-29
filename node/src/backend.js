//const url = "http://localhost:8080"
const url = ""

export function getBooks() {
  return fetch(
    `${url}/books`,
    {
        method: "GET",
        headers: { 'Accept': 'application/json' }
    }
  ).then(resp => resp.json());
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
  fetch(
    `${url}/copies`,
    {
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(copy),
      method: "PUT"
    });
}