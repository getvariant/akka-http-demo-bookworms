import React from "react";
import { Routes, Route } from "react-router-dom";
import {Books} from "./routes/root/Books";
import {BookDetails} from "./routes/books/BookDetails";
import {Checkout} from "./routes/checkout/Checkout";
import {PromoMessage} from "./subheader"
import {UserSelector} from "./footer"

function Router() {
    return (
        <div>
            <PromoMessage />
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