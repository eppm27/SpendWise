import process from "node:process";
import { readFileSync, writeFileSync } from "node:fs";
const url = new URL(process.argv[2] || "");
if (url.protocol !== "https:" || url.username || url.password || url.pathname !== "/" || url.search || url.hash) {
  throw new Error("Provide the HTTPS backend origin only, e.g. https://your-service.onrender.com");
}
const path = new URL("../vercel.json", import.meta.url);
const config = JSON.parse(readFileSync(path, "utf8"));
config.rewrites[0].destination = `${url.origin}/api/:path*`;
writeFileSync(path, JSON.stringify(config, null, 2) + "\n");
console.log("Configured Vercel API proxy for", url.origin);
