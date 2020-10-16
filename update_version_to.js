const fs = require("fs");
const util = require("util");
const exec = util.promisify(require("child_process").exec);
const prettier = require("prettier");

const LERNA_JSON = "./lerna.json";

async function updatePackages(versionArg) {
  await exec(`npx lerna version ${versionArg} --no-push --no-git-tag-version --exact --yes`);
  return require(LERNA_JSON).version;
}

const lernaVersionArg = process.argv[2];
if (!lernaVersionArg) {
  console.error("Missing Lerna's version argument.");
  return 1;
}

function red(str) {
  return ["\x1b[31m", str, "\x1b[0m"];
}

Promise.resolve()
  .then(() => updatePackages(lernaVersionArg))
  .then(version => {
    console.error("");
    console.info(`Updated to '${version}'.`);
  })
  .catch(error => {
    console.error(error);
    console.error("");
    console.error(...red("Error updating versions. There might be undesired changes."));
  })
  .finally(() => {
    console.error("");
  });
