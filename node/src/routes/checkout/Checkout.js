import { useParams } from "react-router-dom";
import { getBookDetails } from "../../backend.js";

export function Checkout() {
  const params = useParams();
  const bookDetails = JSON.parse(localStorage.getItem('book'));
  const book = bookDetails.book;
  const copy = bookDetails.availableCopies.find(elem => elem.id == params.id);
  const tax = (copy.price * 0.097).toFixed(2);
  const shipping = (Math.random() * 12).toFixed(2);
  const total = copy.price + tax + shipping;
    return (
      <table className="book-detail">
        <tbody>
            <tr>
              <td>
                <img src={book.coverImageUri}/>
              </td>
              <td>
                <h2>{book.title}</h2>
                <h3>{book.authors.map(a => `${a.first} ${a.last}`).join(', ')}</h3>
                <h4>ISBN: {book.isbn}</h4>
                <h4>Publication Date: {bookDetails.book.pubDate}</h4>
                <br/>
                Ships from <b>{copy.location}</b> in 2-5 business days
                <br/>
                <table className="bill">
                  <tbody>
                    <tr>
                      <td>Item</td>
                      <td>{copy.price}</td>
                    </tr><tr>
                      <td>Tax</td>
                      <td>{tax}</td>
                    </tr><tr>
                      <td>Shipping</td>
                      <td>{shipping}</td>
                    </tr><tr>
                      <td>Total</td>
                      <td>{total}</td>
                    </tr>
                  </tbody>
                </table>
                <button onClick={buy}>Buy</button>
              </td>
            </tr>
        </tbody>
      </table>
    );

}

function buy() {
    alert("hello");
}
