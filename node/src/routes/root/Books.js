import { useEffect, useState } from 'react';
import { Book } from "./Book";
import { getBooks } from "../../backend.js";

export function Books() {
  const [books, setBooks] = useState(null);
  useEffect(() => {
    const fetchData = async () => {
      const newBooks = await getBooks();
      setBooks(newBooks);
    };
    fetchData();
  }, []);

  if (books) {
    return (
    <div>
      <table className="books-inventory">
        <thead>
          <tr>
            <th>Title</th>
            <th>Authors</th>
            <th>Pub Year</th>
            <th>Copies</th>
          </tr>
        </thead>
        <tbody>
          {books.map(book => <Book key={book.id} book={book} />)}
        </tbody>
      </table>
    </div>
    );
  } else {
    return null;
  }
}

