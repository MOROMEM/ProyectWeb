const token = localStorage.getItem("token");
fetch("/solicitudes", {
  headers: {
    Authorization: `Bearer ${token}`,
  },
})
  .then((response) => response.json())
  .then((data) => {
    console.log(data);
  });
