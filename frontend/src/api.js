import axios from "axios";


const API_URL = "http://localhost:8080/api";


export const fetchEmails = () => axios.get(`${API_URL}/emails`);