#!/usr/bin/fish

make

cd testing

python3 tester.py samples/test02-basic-checkout.in
python3 tester.py samples/test03-basic-log.in
python3 tester.py samples/test12-add-status.in
python3 tester.py samples/test13-remove-status.in
python3 tester.py samples/test14-add-remove-status.in
python3 tester.py samples/test15-remove-add-status.in
python3 tester.py samples/test16-empty-commit-err.in
python3 tester.py samples/test17-empty-commit-message-err.in
python3 tester.py samples/test18-nop-add.in
python3 tester.py samples/test19-add-missing-err.in
python3 tester.py samples/test20-status-after-commit.in
python3 tester.py samples/test21-nop-remove-err.in
python3 tester.py samples/test21-nop-remove-err.in
python3 tester.py samples/test22-remove-deleted-file.in
python3 tester.py samples/test23-global-log.in
python3 tester.py samples/test24-global-log-prev.in
python3 tester.py samples/test25-successful-find.in
python3 tester.py samples/test26-successful-find-orphan.in
python3 tester.py samples/test27-unsuccessful-find-err.in
python3 tester.py samples/test28-checkout-detail.in
python3 tester.py samples/test29-bad-checkouts-err.in
python3 tester.py samples/test30-branches.in
python3 tester.py samples/test30-rm-branch.in
python3 tester.py samples/test31-duplicate-branch-err.in
python3 tester.py samples/test31-rm-branch-err.in
python3 tester.py  samples/test32-file-overwrite-err.in
python3 tester.py samples/test37-reset1.in
python3 tester.py samples/test38-bad-resets-err.in
python3 tester.py samples/test39-short-uid.in
python3 tester.py samples/test41-no-command-err.in
python3 tester.py samples/test42-other-err.in

# python3 tester.py samples/test33-merge-no-conflicts.in
# python3 tester.py samples/test34-merge-conflicts.in
# python3 tester.py samples/test35-merge-rm-conflicts.in
# python3 tester.py samples/test36-merge-err.in
# python3 tester.py samples/test36-merge-parent2.in

# python3 tester.py samples/test40-special-merge-cases.in
# python3 tester.py samples/test43-criss-cross-merge.in
# python3 tester.py samples/test43-criss-cross-merge-b.in
# python3 tester.py samples/test44-bai-merge.in

cd ..