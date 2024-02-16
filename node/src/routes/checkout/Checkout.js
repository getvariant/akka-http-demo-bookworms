import { useParams } from "react-router-dom";
import { useEffect, useState } from 'react';
import { putCopyHold, buyCopy } from "../../backend.js";

export function Checkout() {
  const params = useParams();

  const [receipt, setReceipt] = useState(null);
  useEffect(() => {
      const fetchData = async () => {
      const newReceipt = await putCopyHold(params.id);
      setReceipt(newReceipt);
   };
    fetchData();
  }, []);

      if (receipt) {
      const bookDetails = JSON.parse(localStorage.getItem('book'));
      const book = bookDetails.book;
      const copy = bookDetails.availableCopies.find(elem => elem.id == params.id);
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
                    {receipt.withReputation &&
                        <div>
                            Sold by <b>{copy.seller}</b>
                            <span className="reputation">{'\u2605'.repeat(copy.reputation) + '\u2606'.repeat(5 - copy.reputation)}</span>
                        </div>
                    }
                    Ships from <b>{copy.location}</b> in 2-5 business days
                    <br/>
                    <table className="bill">
                      <tbody>
                        <tr>
                          <td>Item</td>
                          <td>{receipt.price}</td>
                        </tr><tr>
                          <td>Tax</td>
                          <td>{receipt.tax}</td>
                        </tr><tr>
                          <td>Shipping</td>
                          <td>{receipt.shipping}</td>
                        </tr><tr>
                          <td><b>Total</b></td>
                          <td><b>{receipt.total}</b></td>
                        </tr>
                      </tbody>
                    </table>
                    <button onClick={() => buy(copy)}>Buy</button>
                    {receipt.suggestions.length > 0 &&
                        <table>
                          <tbody>
                              <tr>
                                <td colSpan="3">
                                  Customers who bought <b>{book.title}</b><br/>
                                  also bought:
                                </td>
                              </tr>
                              <tr>
                                {
                                  receipt.suggestions.map(
                                   book =>
                                    <td >
                                      <img width="160px" src={book.coverImageUri}/>
                                    </td>
                                  )
                                }
                              </tr>
                              <tr>
                              </tr>
                          </tbody>
                        </table>
                    }
                  </td>
                </tr>
            </tbody>
          </table>
        );
    } else {
      return null;
    }
}

function buy(copy) {
  buyCopy(copy).then(resp => window.location="/");
}

