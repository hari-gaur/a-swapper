# Settings #
| **Name** | Meaning with swap file | Meaning with swap partition |
|:---------|:-----------------------|:----------------------------|
| Run swapper at startup | Launch swapper at startup and try to create/use swap file | Launch swapper at startup and try to enable swap partition |
| Swap place | Where to place swap file | _none_                      |
| Swap size | Size of swap file      | _none_                      |
| Swappiness | How much to swap - 0-100 | How much to swap - 0-100    |
| Use swap partition | If disabled, using swap file settings | If enabled, using swap partition settings |
| Swap partition | _none_                 | Which partition to use for swap |
| Recreate swap file | Each time delete and create swap file | _none_                      |
| Reformat swap | Make swap file system in swap file | Make swap file system on partition|

# Misuse #

  * Swapper doesn't create swap partition, it should be already available.
  * Swap partition is not same as ext2/3 partition, it normally doesn't mount in linux, it contains no files.
  * Swapper can format any existing partition to swap partition, but **ALL DATA ON THAT PARTITION WILL BE LOST**
  * With enabled "Recreate swap file" but disabled "Reformat swap" created swap file will not be of any use (enabling swap will fail!)