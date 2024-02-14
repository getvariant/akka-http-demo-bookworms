import { useEffect, useState } from 'react';
import { getPromoMessage } from "./backend.js";

export function PromoMessage() {

    // This API call happens outside the context of a particular page and concurrently with the main
    // page API call. This presents a race condition: we want the main call to have completed and returned
    // the session ID cookie before this call is made, so that the session ID cookie would be available on
    // the backend of this call, so that Variant session, and with it the current state request be available.
    const [promoMessage, setPromoMessage] = useState(null);
    useEffect(() => {
      setTimeout(
        () => {
          const fetchData = async () => {
            const promoMessage = await getPromoMessage()
            document.getElementById("promo").innerHTML = promoMessage
          };
          fetchData()
        }, 500)
      }, []);

    return (
        <div id={'promo'}></div>
    );
}
