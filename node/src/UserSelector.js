import { useEffect, useState } from 'react';
import { getUser, setUser } from "./backend.js";

function UserSelector() {

    function onChangeListener(e) {
        // Change user on the server
        setUser(e.target.value);
        // Close current variant session by deleting the session id cookie.
        document.cookie = "variant-ssnid=;host=loclhost;path=/;expires=" + new Date(0).toUTCString()
        // Make the new select setting stick
        setCurrentUser(e.target.value);
    }

    const [currentUser, setCurrentUser] = useState(null);
    useEffect(() => {
        const fetchData = async () => {
          const currUser = await getUser()
          setCurrentUser(currUser)
        };
        fetchData()
      }, []);

    if (currentUser) {
        return (
            <label id={'user-select'}>
            Current user:
            <select

              value={currentUser}
              onChange={e => onChangeListener(e)}
            >
              <option value="Everyman">Everyman</option>
              <option value="WithSuggestions">WithSuggestions</option>
              <option value="WithReputation">WithReputation</option>
            </select>
            </label>
        );
    } else {
        return null;
    }
}

export default UserSelector;