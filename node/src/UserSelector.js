import { useEffect, useState } from 'react';
import { getUser, setUser } from "./backend.js";

function UserSelector() {

    function onChangeListener(e) {
        document.cookie = "variant-ssnid=;host=loclhost;path=/;expires=" + new Date(0).toUTCString()
        console.log(document.cookie)
        setUser(e.target.value);
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
              <option value="random">random</option>
              <option value="qualifiedForTest">qualifiedForTest</option>
              <option value="disqualifiedForTest">disqualifiedForTest</option>
            </select>
            </label>
        );
    } else {
        return null;
    }
}

export default UserSelector;