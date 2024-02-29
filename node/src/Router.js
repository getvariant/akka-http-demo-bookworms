import React from "react";
import { Routes, Route } from "react-router-dom";
import {Books} from "./routes/root/Books";
import {BookDetails} from "./routes/books/BookDetails";
import {Checkout} from "./routes/checkout/Checkout";
import {UserSelector} from "./footer"

function Router() {
    return (
        <div>
            <div style={{display:'none'}} id={'promo'}></div>
            <Routes>
                <Route path="/" element={<Books />} />
                <Route path="books/:id" element={<BookDetails />} />
                <Route path="checkout/:id" element={<Checkout />} />
            </Routes>
            <UserSelector />
        </div>
    );
}

export default Router;