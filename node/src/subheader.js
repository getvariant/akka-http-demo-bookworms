import { useEffect, useState } from 'react';
import { getPromoMessage } from "./backend.js";

export function PromoMessage() {

    const [promoMessage, setPromoMessage] = useState(null);
    useEffect(() => {
      const fetchData = async () => {
        const fromServer = await getPromoMessage()
        setPromoMessage(fromServer)
       }
       fetchData()
      }, []);

    if (promoMessage) {
        return (
            <div id={'promo'}>
                {promoMessage}
            </div>
        );
    } else {
        return null;
    }
}
